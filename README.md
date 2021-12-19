cd front
docker build . -t my-next-js-app
docker run -p 3000:3000 my-next-js-app
