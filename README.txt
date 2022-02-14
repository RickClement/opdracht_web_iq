A simple Java web crawler that takes a web page and returns a number of the most important words on that page.
This ranking is calculated using the TFIDF scores of each word on the page, with the crawled pages used as the corpus.

Built using Java JDK 11, Apache NetBeans 12.6 and Maven 3.6.3
Uses the Jsoup version 1.14.3 library.

Usage:
In the command line, use the command:
java -jar target/opdracht_web_iq-1.0.jar [WEB PAGE] -r [RANK] -d [DEPTH] -t [TIME]

Mandatory arguments:
- Web page: The web page to be crawled.

Optional arguments:
- Rank: the number of terms returned, ranked by TFIDF score (command: "-r", default value: 5).
- Depth: the maximum depth to be crawled; includes the main page (command: "-d", default value: 2).
- Time: the maximum amount of time spent crawling pages and gathering the corpus in seconds (command: "-t", default value: 300).
