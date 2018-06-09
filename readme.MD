
****Realtime statistics of online transactions****

This is my solution for a coding challenge that requires to receive transactions of the last 60 seconds and to return the overall summary statistics which contains total sum, min, max, total count and average in constant time and memory complexity or simply O(1). Moreover, it could be called through restful endpoints as follows:

**POST /transactions**\
●201 - in case of success\
●204 - if transaction is older than 60 seconds\
 Body:
 e.g.{ 
       ​ "amount": 12.3,
 "timestamp": 1478192204000
 }
 
 **GET /statistics**\
 Returns:\
 e.g.{
 "sum": 1000,
 "avg": 100,
 "max": 200,
 "min": 50,
 "count": 10
 }
 
 BTW, to run this application please follow Spring Boot documents and feel free to contact me or to send your pull request if you come up with any better idea or more efficient approach.