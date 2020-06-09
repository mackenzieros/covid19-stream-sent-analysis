# Covid-19 Tweet Stream Sentiment Analyzer
## Overview
This project aims to produce and archive COVID-19-related tweet sentiment by geolocation and day within the US for analysis. This is a tool used for a project correlating sentiment and case numbers of the virus. 
## Features
- Tweet stream data is collected from Twitter's COVID-19 Stream API and processed using Apache Kafka's Streams DSL API
- Tweet sentiment across the 50 states is plotted day-by-day against each states' case numbers
## Running this project locally
##### From the repo:
I plan to containerize the project so it will contain all of the necessary dependencies and topics. You would, however, need access to Twitter's Developer API at: https://developer.twitter.com/en
## To-Do:
- [x] Create first round of tests
- [x] Configure main stream topology
- [ ] Get access to Twitter's COVID-19 stream API
- [ ] Configure topic partitions
- [ ] Create topics for 50 states
- [ ] Create script to write topic messages to files
- [ ] Containerize
