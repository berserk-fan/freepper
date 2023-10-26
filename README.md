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
