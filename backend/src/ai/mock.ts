const humanMessage = 'develop the modal';

const issue =
  'Develop a modal with the text hello world and a button that says close';

const branchChanges = [''];

const allFiles = [
  'package.json',
  'src/index.js',
  'src/App.js',
  'public/index.html',
  'src/App.css',
];

const code = [
  {
    name: 'package.json',
    content:
      '{\n  "name": "react-app",\n  "version": "0.1.0",\n  "private": true,\n  "dependencies": {\n    "react": "^18.0.0",\n    "react-dom": "^18.0.0",\n    "react-scripts": "5.0.0"\n  },\n  "scripts": {\n    "start": "react-scripts start",\n    "build": "react-scripts build",\n    "test": "react-scripts test",\n    "eject": "react-scripts eject"\n  }\n}',
  },
  {
    name: 'src/index.js',
    content:
      "import React from 'react';\nimport ReactDOM from 'react-dom';\nimport App from './App';\n\nReactDOM.render(\n  <React.StrictMode>\n    <App />\n  </React.StrictMode>,\n  document.getElementById('root')\n);",
  },
  {
    name: 'src/App.js',
    content:
      "import React from 'react';\n\nfunction App() {\n  return (\n    <div>\n      <h1>Welcome to React</h1>\n      <p>This is a basic React app.</p>\n    </div>\n  );\n}\n\nexport default App;",
  },
  {
    name: 'public/index.html',
    content:
      '<!DOCTYPE html>\n<html lang="en">\n  <head>\n    <meta charset="UTF-8" />\n    <meta name="viewport" content="width=device-width, initial-scale=1.0" />\n    <title>React App</title>\n  </head>\n  <body>\n    <div id="root"></div>\n  </body>\n</html>',
  },
  {
    name: 'src/App.css',
    content:
      'div {\n  text-align: center;\n  font-family: Arial, sans-serif;\n  margin: 20px;\n}\nh1 {\n  color: #61dafb;\n}\n',
  },
];
