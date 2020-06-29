  
#!/usr/bin/bash

# Install packages
yum -y update
amazon-linux-extras install -y java-openjdk11
amazon-linux-extras install -y nginx1
yum install -y java-11-openjdk-devel git
su ec2-user -l -c 'curl -s "https://get.sdkman.io" | bash && source .bashrc && sdk install gradle'
sudo yum install -y postgresql
sudo yum install -y gcc
sudo yum install -y python3-devel
sudo yum install -y postgresql-devel
sudo yum install -y jq # json parser
sudo pip3 install psycopg2
sudo pip3 install boto3
cd /home/ec2-user
pip3 install boto3 --user
pip3 install psycopg2 --user



# Configure/install custom software
cd /home/ec2-user
git clone https://github.com/cdavidshaffer/java-image-gallery.git
git clone https://github.com/mlukacsko/ansible.git
chown -R ec2-user:ec2-user java-image-gallery
chown -R ec2-user:ec2-user ansible

CONFIG_BUCKET="s3://edu.au.cc.image-gallery-config"
aws s3 cp ${CONFIG_BUCKET}/nginx/nginx.conf /etc/nginx/nginx.conf
aws s3 cp ${CONFIG_BUCKET}/nginx/default.d/image_gallery.conf /etc/nginx/default.d/image_gallery.conf

# Start/enable services
systemctl stop postfix
systemctl disable postfix
systemctl start nginx
systemctl enable nginx

su ec2-user -l -c 'cd ~/java-image-gallery && ./start' >/var/log/image_gallery.log 2>&1 &
