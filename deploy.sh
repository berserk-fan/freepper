set -e -x
rm -rf ./**
export ENV=Dev
echo "buzz qqqq"
aws s3 cp $s3path $file_name
unzip $file_name
folder_name="${file_name%.zip}"
cd $folder_name
log_file="../$folder_name.log" 
bash deployment/ec2/start.sh
echo "finished from start.sh"
echo "exiting"
exit
