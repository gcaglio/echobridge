# Echobridge - what is
A java server to emulate multiple switch to Amazon Echo devices
This program is a "bridge" between the Echo devices and any other software component that you may need to intaract with from the echo devices. 
For example :
- run a report
- send a notification (mqtt, https, etc)
- whatever you need

# How to use
1. download and compile
2. edit the sample "configuration.xml" file adding as many virtual switch as you need
3. run the program with : java EchoBridgeServer /path/to/the/configuration.xml

# How it works
- The program start an UDP broacast responder thread, to let the Echo Devices discover the virtual switches
- The program start multiple HTTP server thread (one for each virtual switch) to let the Echo devices interact with the virtual devices

# Device types
At the moment i've only two classes hierarchy : 
- net.cantylab.devices.DummyDevice that basically do nothing except keeping track of the status changes.
- net.cantylab.devices.UrlDevice that allow you to call a GET/POST url with parameters and to check the output for a specific java regular expression. You could use this class in the configuration file to invoke Url get or post when Amazon Echo invoke the "turn on" or "turn off" action on your virtual switch
- net.cantylab.devices.MqttDevice that allow you to publish a message to an MQTT topic. If the publish code ends without errors (=the message is succesfully published) the device set its state accordingly. You could use this class in the configuration file to publish messages to a specific topic invoking Alexa's turn-on/turn-off command.

You could easily implement your "device" class extending the net.cantylab.AbstractDevice class, that implements the net.cantylab.Device interface.

Please keep in mind that the Amazon Echo devices need to have the response back to their invocation as soon as possibile: you cannot invoke a long running job. In this scenario you need to start an async thread that basically do what you need to be done and update the "status" variable. 


# Latest Changes 
In the next releases i will add support for :
- Dec/2020 : implemented MQTT client support
  NOTE : the MqttDevice class implements its functionalities using the MQTT_PAHO_CLIENT library (not included)
- Dec/2020 : fixed Alexa payload string buffer length bug

