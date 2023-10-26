# description
this is a fullstack application for a web store
it uses next.js for frontend and a scala+postgres for backend
frontend communicate with backed using grpc-web and protobuf based apis
envoy server handled grpc-web -> grpc transformation

# how to run
 
  1. docker build . -t my-next-js-app
  1. docker run -p 3000:3000 my-next-js-app

# TODO
extract envoy deployment

