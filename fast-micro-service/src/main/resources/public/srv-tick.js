function subscribe () {
    console.log("Subscribing to event bus");
    var eb = new EventBus('http://localhost:8080/tick');
    eb.enableReconnect(true);


    eb.onopen = function () {
        console.log("Connected to event bus");
        eb.registerHandler('tick-address', function (error, message) {
            if(error) {
                console.log('Error listening on: tick-address', error);
            } else {
                console.log('Received the message: ', message);
            }

        });

        eb.registerHandler('tick-address-1', function (error, message) {
            if(error) {
                console.log('Error listening on: tick-address', error);
            } else {
                console.log('Received the message: ', message);
            }

        })
    };
}

