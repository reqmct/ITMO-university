window.notify = function (message) {
    $.notify(message, {
        position: "right bottom",
        className: "success"
    });
}

window.ajax = function (data, $error, responseFunction) {
    $.ajax({
        type: "POST",
        url: "",
        dataType: "json",
        data: data,
        success: function (response) {
            if (response["error"]) {
                $error.text(response["error"]);
            }
            if (responseFunction) {
                responseFunction(response);
            }
            if (response["redirect"]) {
                location.href = response["redirect"];
            }
        }
    });
}
