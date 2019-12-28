# Echobridge - what is
A java server to emulate multiple switch to Amazon Echo devices
This program is a "bridge" between the Echo devices and any other software component that you may need to intaract with from the echo devices. 
For example :
- run a report
- send a notification (mqtt, https, etc)
...

# How to use
1. download and compile
2. edit the sample "configuration.xml" file adding as many virtual switch as you need
3. run the program with : java EchoBridgeServer /path/to/the/configuration.xml

# How it works
- The program start an UDP broacast responder thread, to let the Echo Devices discover the virtual switches
- The program start multiple HTTP server thread (one for each virtual switch) to let the Echo devices interact with the virtual devices

# TODO
At the moment i've only a "DummyDevice" that track the turnon-turnoff requests, change state but do substiantially nothing.
In the next releases i will add support for :
- MQTT client
- HTTP/HTTPS interaction
You could easily implement your logic cloning the DummyDevice class, adding your specific code

