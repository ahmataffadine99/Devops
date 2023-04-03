#image for Poll

FROM python:3

#DIrectory

WORKDIR /app

#Dependencies

COPY requirements.txt requirements.txt

RUN pip3 install -r requirements.txt

#Bundle a source

COPY . .

#port
EXPOSE 80

CMD ["flask", "run" , "--host=0.0.0.0", "--port=80"]
