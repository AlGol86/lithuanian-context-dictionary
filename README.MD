choose and download dataset:
https://opus.nlpl.eu/corpora-search/lt&ru
recommended: **OpenSubtitles v2024**; good and large: **NLLB v1**

To run application locally:
C:\path-to-jdk\.jdks\openjdk-24.0.1\bin\java.exe -Xms12g -Xmx16g "-Ddataset-lt.absolute-path=C:\tmp\txtcorpus\lt-ru.txt\NLLB.lt-ru.lt" "-Ddataset-ru.absolute-path=C:\tmp\txtcorpus\lt-ru.txt\NLLB.lt-ru.ru" -jar "C:\path-to-jar\ltdictionary-0.0.1-SNAPSHOT.jar"
