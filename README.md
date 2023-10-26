# description
this is a fullstack application for a web store
it uses next.js for frontend and a scala+postgres for backend
frontend communicate with backed using grpc-web and protobuf based apis
envoy server handled grpc-web -> grpc transformation

# ci/cd
we use github actions for backend and vercel for frontend
we upload build outputs to the S3

# deployment
we use github action to deploy to EC2. we use bash scripts and systemd because it's cheaper and faster than docker

#directory structure
frontend next.js app + several aws-lambdas under front
backend service under /services/catalog

#authentication
authentication implementation is ongoing, for the most part it's next-auth integrated with our custom backend that allows to easily implement
oauth2 for dozens of providers, implemenent email authentication and finally basic email based authentication. look and the pull request for some code. 
this pull request also adds scala 3

#api
you may look at the protobuf files in the project for our apis

#unit tests
I implemented automated repository unit testing, also we have a lot of integration tests
