# Directory Structure
When we deploy our app we have the following structure of files on server

    - bin
        some-file
        some-other-file.py
    - server
        bin // this is from native packager
        ...
    - logs
        some-java-log-file.log
        some-envoy-log-file.log
    - deployment //copy of our deployment folder
        common
        ec2
