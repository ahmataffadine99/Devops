#image for Result

FROM node:12-alpine

#DIrectory

WORKDIR /app

#Dependencies

COPY package*.json ./

RUN npm install

#Bundle a source

COPY . .

#port

EXPOSE 80


CMD [ "node", "server.js"]

