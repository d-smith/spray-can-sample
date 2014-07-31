Examples to illustrate how to create simple services using spray-can as the http front end to Akka,
as well as using routes, directives, JSON serialization, etc.

Note that you can post a json payload to the JsonSampleServer using curl like thus:

        curl -vX POST http://localhost:8666/addnote --data @addnote.json -H "Content-Type:application/json"

The above command assumes there's a file named addnote.json containing the json payload located
in the current directory.

For the access token service:

      curl -vX POST http://localhost:8666/token -H "Content-Type:application/x-www-form-urlencoded" --data @access

