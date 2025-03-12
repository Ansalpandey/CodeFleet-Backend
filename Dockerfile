FROM ubuntu:latest
LABEL authors="pande"

ENTRYPOINT ["top", "-b"]