# using node alpine so the image is small
FROM node:18-alpine

WORKDIR /app
COPY package*.json ./
RUN npm install

# copy rest of the code
COPY . .

EXPOSE 5173
# run vite dev server and listen on all interfaces (needed in docker)
CMD ["npm", "run", "dev", "--", "--host"]