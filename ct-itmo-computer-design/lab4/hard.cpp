#include <iostream>
#include <fstream>
#include <vector>
#include <omp.h>
#include <stdexcept>
using namespace std;

string version;
int width;
int height;
int colorsNumber;
int histogram[256];
double p[256];
double q[256];
double mu[256];
int threads = 0;
vector<unsigned char> img;

void recount() {
	for (int f = 0; f < 256; f++) {
		p[f] = double(histogram[f]) / (width * height);
		if (f != 0) {
			q[f] = q[f - 1];
			mu[f] = mu[f - 1];
		}
		q[f] += p[f];
		mu[f] += f * p[f];
	}
}

double getP(int f) {
	return p[f];
}

double getQ(int startf, int finishf) {
	if (startf == 0) {
		return q[finishf];
	}
	return q[finishf] - q[startf - 1];
}

double getMu(int startf, int finishf) {
	double qCopy = getQ(startf, finishf);
	if (qCopy == 0) {
		return -1;
	}
	double sum = mu[finishf];
	if (startf != 0) {
		sum = sum - mu[startf - 1];
	}
	return sum / qCopy;
}


double getSigma(int startf, int finishf) {
	double copyMu = getMu(startf, finishf);
	if (copyMu == -1) {
		return -1;
	}
	return  copyMu * copyMu * getQ(startf, finishf);
}

void getBestFWithOmp(int bestF[3]) {
	double bestSigma = 0;
	if (threads != 0) {
		omp_set_num_threads(threads);
	}
#pragma omp parallel for schedule(dynamic)
	for (int f1 = 0; f1 <= colorsNumber; f1++) {
		double sigma1 = getSigma(0, f1);
		if (sigma1 == -1) {
			continue;
		}
		for (int f2 = f1 + 1; f2 <= colorsNumber; f2++) {
			double sigma2 = getSigma(f1 + 1, f2);
			if (sigma2 == -1) {
				continue;
			}
			for (int f3 = f2 + 1; f3 <= colorsNumber; f3++) {
				double sigma3 = getSigma(f2 + 1, f3);
				double sigma4 = getSigma(f3 + 1, colorsNumber);
				if (sigma3 == -1 || sigma4 == -1) {
					continue;
				}
				double sigma = sigma1 + sigma2 + sigma3 + sigma4;
				if (sigma > bestSigma) {
#pragma omp critical 
					{
						if (sigma > bestSigma) {
							bestSigma = sigma;
							bestF[0] = f1;
							bestF[1] = f2;
							bestF[2] = f3;
						}
					}
				}
			}
		}
	}
}

void getBestF(int bestF[3]) {
	if (threads != -1) {
		getBestFWithOmp(bestF);
		return;
	}
	double bestSigma = 0;
	for (int f1 = 0; f1 <= colorsNumber; f1++) {
		double sigma1 = getSigma(0, f1);
		if (sigma1 == -1) {
			continue;
		}
		for (int f2 = f1 + 1; f2 <= colorsNumber; f2++) {
			double sigma2 = getSigma(f1 + 1, f2);
			if (sigma2 == -1) {
				continue;
			}
			for (int f3 = f2 + 1; f3 <= colorsNumber; f3++) {
				double sigma3 = getSigma(f2 + 1, f3);
				double sigma4 = getSigma(f3 + 1, colorsNumber);
				if (sigma3 == -1 || sigma4 == -1) {
					continue;
				}
				double sigma = sigma1 + sigma2 + sigma3 + sigma4;
				if (sigma > bestSigma) {
					bestSigma = sigma;
					bestF[0] = f1;
					bestF[1] = f2;
					bestF[2] = f3;
				}
			}
		}
	}
}

unsigned char convert(unsigned char c, int f[3]) {
	if (c <= f[0]) {
		return 0;
	}
	if (c <= f[1]) {
		return 84;
	}
	if (c <= f[2]) {
		return 170;
	}
	return 255;
}

void convertImg(int f[3]) {
	if (threads >= 1) {
		omp_set_num_threads(threads);
	}
#pragma omp parallel for if (threads >= 0)
	for (int i = 0; i < img.size(); i++) {
		img[i] = convert(img[i], f);
	}
}


void readFile(string source) {
	ifstream in(source);

	if (!in) {
		throw invalid_argument("Failed to open file " + source);
	}

	in >> version;

	if (version != "P5") {
		throw invalid_argument("Invalid file version " + source);
	}

	if (!(in >> width >> height >> colorsNumber)) {
		throw invalid_argument("File is not supported: invalid data");
	}

	if (!in.get() || colorsNumber != 255) {
		throw invalid_argument("File is not supported: invalid data");
	}

	char color;

	while (in.get(color)) {
		img.push_back((unsigned char)(color));
	}

	if (img.size() != width * height) {
		throw invalid_argument("File is not supported: invalid data");
	}

	in.close();
}

void getHistogram() {
	for (int i = 0; i < img.size(); i++) {
		histogram[img[i]]++;
	}
}

void outFile(string source) {
	ofstream out(source);
	out << version << "\n";
	out << width << " " << height << "\n" << colorsNumber << "\n";
	for (int i = 0; i < img.size(); i++) {
		out << img[i];
	}
	if (out.bad()) {
		throw invalid_argument("Writing to " + source + " failed");
	}
	out.close();
}

int main(int argc, char* argv[]) {
	if (argc != 4) {
		cerr << "Incorrect number of arguments";
		return -1;
	}
	try {
		threads = atoi(argv[1]);
	}
	catch (exception& e) {
		cerr << "Incorrect number of threads";
		return -1;
	}
	try {
		readFile(argv[2]);
	}
	catch (invalid_argument& e) {
		cerr << e.what() << endl;
		return -1;
	}
	int f[3];
	double start = omp_get_wtime();
	getHistogram();
	recount();
	getBestF(f);
	convertImg(f);
	printf("%d %d %d\n", f[0], f[1], f[2]);
	printf("Time (%i thread(s)): %g ms\n", threads, (omp_get_wtime() - start) * 1000);
	try {
		outFile(argv[3]);
	}
	catch (invalid_argument& e) {
		cerr << e.what() << endl;
		return -1;
	}
}