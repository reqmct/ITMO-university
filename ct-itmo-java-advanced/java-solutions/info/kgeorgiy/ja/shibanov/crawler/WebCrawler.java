package info.kgeorgiy.ja.shibanov.crawler;

import info.kgeorgiy.java.advanced.crawler.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.*;

public class WebCrawler implements NewCrawler {

    private final Downloader downloader;
    private final ExecutorService downloaders;
    private final ExecutorService extractors;
    private final int perHost;
    private final ConcurrentMap<String, ExecutorServiceWithLimit> hostLimits;

    public WebCrawler(Downloader downloader, int downloaders, int extractors, int perHost) {
        this.downloader = downloader;
        this.downloaders = Executors.newFixedThreadPool(downloaders);
        this.extractors = Executors.newFixedThreadPool(extractors);
        this.perHost = perHost;
        this.hostLimits = new ConcurrentHashMap<>();
    }

    private void hostClear() {
        hostLimits.values().forEach(ExecutorServiceWithLimit::clear);
    }

    @Override
    public Result download(String url, int depth, Set<String> excludes) {
        var result = new SingleUseDownloader(depth, excludes).recursiveDownload(url);
        hostClear();
        return result;
    }

    @Override
    public void close() {
        downloaders.shutdown();
        extractors.shutdown();
    }

    private class SingleUseDownloader {
        private final Set<String> excludes;
        private final int depth;
        private final ConcurrentMap<String, Boolean> successfulDownload = new ConcurrentHashMap<>(); // :NOTE: Set?
        // :ANS: проблема SET в модификации в erros нужно добавлять доп excludes url, что не проходит тесты | иной вариант создание сета для excludes
        private final Map<String, IOException> errors = new ConcurrentHashMap<>();
        private final Queue<String> queue = new ConcurrentLinkedQueue<>();
        private final Phaser phaser = new Phaser();


        public SingleUseDownloader(int depth, Set<String> excludes) {
            this.excludes = excludes;
            this.depth = depth;
        }

        private boolean inExcludes(String url) {
            return excludes.stream().anyMatch(url::contains);
        }

        private void download(String url, int currentDepth) {
            if (inExcludes(url)) {
                return;
            }
            try {
                String host = URLUtils.getHost(url);
                phaser.register();
                var downloadersWithLimit = hostLimits.computeIfAbsent(host,
                        (ignored) -> new ExecutorServiceWithLimit(downloaders, perHost));
                downloadersWithLimit.submit(downloaderLogic(url, currentDepth));
            } catch (MalformedURLException e) {
                errors.put(url, e);
            }
        }

        private Runnable downloaderLogic(String url, int curr) {
            return () -> {
                try {
                    Document document = downloader.download(url);
                    successfulDownload.put(url, true);
                    if (curr < depth - 1) {
                        phaser.register();
                        extractors.submit(extractorLogic(url, document));
                    }
                } catch (IOException e) {
                    errors.put(url, e);
                } finally {
                    phaser.arriveAndDeregister();
                }
            };
        }

        private Runnable extractorLogic(String url, Document document) {
            return () -> {
                try {
                    for (var link : document.extractLinks()) {
                        successfulDownload.computeIfAbsent(link,
                                (ignored) -> {
                                    queue.add(link);
                                    return false;
                                }
                        );
                    }
                } catch (IOException e) {
                    errors.put(url, e);
                } finally {
                    phaser.arriveAndDeregister();
                }
            };
        }

        public Result recursiveDownload(String url) {
            phaser.register();
            queue.add(url);
            for (int curr = 0; curr < depth; curr++) {
                ArrayList<String> links = new ArrayList<>(queue);
                queue.clear();
                for (var link : links) {
                    download(link, curr);
                }
                phaser.arriveAndAwaitAdvance();
            }
            return new Result(successfulDownload.entrySet()
                    .stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .toList(),
                    errors);
        }
    }

    public static void main(String[] args) {
        if (args == null || args.length < 1 || args.length > 5) {
            System.err.println("Incorrect args");
            return;
        }
        List<Integer> list = new ArrayList<>();
        final int defaultValue = 1;
        for (int i = 1; i <= 4; i++) {
            if (i >= args.length) {
                list.add(defaultValue);
                continue;
            }
            try {
                // :NOTE: WebCrawler url [depth [downloads [extractors [perHost]]]]
                list.add(Integer.parseInt(args[i]));
            } catch (NumberFormatException e) {
                return;
            }
        }
        try (WebCrawler webCrawler = new WebCrawler(new CachingDownloader(1000), list.get(1), list.get(2), list.get(3))) {
            webCrawler.download(args[0], list.getFirst());
        } catch (IOException e) {
            System.err.println("Unexpected exception in CachingDownloader: " + e);
        }
    }
}
