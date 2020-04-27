# ADS-B Player
## **WIP**
This player is designed to replay [ADS-B messages.](https://en.wikipedia.org/wiki/Automatic_dependent_surveillance_%E2%80%93_broadcast)  

## Getting Started
Either clone or download this repo. If you download this repo, be sure to unzip it.

You have 2 main build choices. You can either build a distribution package or an uber-jar.

### Distribution Package:
A distribution package will make running the application more convenient. For this, run `./gradlew distZip`. This will create create a a directory tree and a zip file in `build/distributions/ADSB-Player-VERSION.zip`. When this is unzipped, there will be bin and lib directories . Inside of the bin directory there will be two convienience scripts (a .bat file for windows and unix executable) from the command line (in the bin directory) you should be able to run the application as follows: `ADSB-Player -i <<path-to-data-file.txt>>`. See next section for more detailed explanation of arguments and options.

### Uber Jar
An Uber-jar will have all necessary files bundled into one. To get this run `./gradlew uberjar`.  This will create a single jar inside of libs/Player-<<VERSION>>.jar. The rest of these commands assume a Distribution package, but the only change is that instead of running commands like `ADSB-Player` you would prefix that with `java -jar ` so you would run the application with default parameters as follows: `java -jar ADSB-Player<<VERSION>>.jar -i <<path-to-data-file.txt>>`

## Usage
Minimum command to get your messages playing (assuming distribution package) is: `ADSB-Player -i <<path-to-data-file.txt>>` . With the default arguments, this will play a single line of the input file every 5 seconds to any client(s) connected on port 30003.

To change the delay between messages you can add the option `-d OR —delay` followed by the time you’d like in milliseconds. So, to play a line every half second from an input file, you’d have a command that looks like this: `ADSB-Player -i <<path-to-data-file.txt>> -d 500`

Once the end of the file has been reached it will start over at the beginning again. If you’d instead like the application to exit, you’d specify the end behavior with the `-e OR —end-behavior` option. Using the default `r` for this option will restart playback, whereas `e` will end playback.

For default values and help, either run `ADSB-Player` or `ADSB-Player -h`.

