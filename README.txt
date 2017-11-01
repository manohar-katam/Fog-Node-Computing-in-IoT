
1. In order to run this JAR file, you need to have Java installed on your machine (JVM and JRE).

2. You run the IoTNodeRequestGenerator with the following command (for a Unix machine):

java -jar “/Location/of/the/IoTNodeReqGen/dist/IoTNodeReqGen.jar" Location/of/the/config.txt

3. The format of the config file is as follows:
{line1}: “IP address” of the machine where IoTNodeRequestGenerator runs. If you run all the nodes 
             on your machine you can write 127.0.0.1.

{line2}: “Listening Port” of the IoTNodeRequestGenerator to listen to incoming UDP response.
{line3}: “Interval” between sending packets to destinations (in ms).
{line4}: “Forward limit”

{line5}: “Number of fog nodes”. This is used in the program to know how many more lines must be read.
{line6}: “IP address” of the first fog node

{line7}: “UDP port number” of the first fog node

{line8}: “IP address” of the second fog node

{line9}: “UDP port number” of the second fog node

{line10}: “IP address” of the third fog node
{line11}: “UDP port number” of the third fog node

and so on....

4. You run the fogNode with the following command (for a Unix machine):

java “/Location/of/the/fogNode 25 1000 127.0.0.1 3001 5001 127.0.0.1 5002.

5. The arguments to be passed above are in following manner:
{arg1:} "Max_Response time" of the fog node
{arg2:} "periodic update time"
{arg3:} "IP address" of the machine where fogNode runs. If you run all the nodes on your machine you can write 127.0.0.1.

{arg4:} “UDP port number” of the current fog node.
{arg5:} “TCP port number” of the current fog node.
{arg6:} "IP address" of the machine where fogNode runs. If you run all the nodes on your machine you can write 127.0.0.1.

{arg5:} “TCP port number” of the first neighbor fog node.
{arg7:} "IP address" of the machine where fogNode runs. If you run all the nodes on your machine you can write 127.0.0.1.

{arg8:} “TCP port number” of the second neighbor fog node.
and so on....