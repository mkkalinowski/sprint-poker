docker pull pandeiro/lein
docker run -d -v $(pwd):/root -w /root -p 3000:3000 pandeiro/lein lein ring server-headless
