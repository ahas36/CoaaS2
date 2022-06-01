FROM ubuntu

# Copying the requirements file to install them
COPY . /app/
WORKDIR /app/

# Installing python
RUN apt-get update -y
RUN apt-get install -y python3 python3-pip
RUN pip3 install --upgrade setuptools
RUN pip3 install --upgrade pip

# Change working directory
WORKDIR /app/

# Install the libaries in reqiurements.txt
RUN pip3 install -r requirements.txt

# Run generator
CMD [ "chmod +x queryGenerator.bash" ]
CMD [ "bash queryGenerator" ]