./gradlew bootJar

aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin 533267191976.dkr.ecr.ap-northeast-2.amazonaws.com

docker build --platform amd64 -t reddi-server .

docker tag reddi-server:latest 533267191976.dkr.ecr.ap-northeast-2.amazonaws.com/reddi-server:latest

docker push 533267191976.dkr.ecr.ap-northeast-2.amazonaws.com/reddi-server:latest