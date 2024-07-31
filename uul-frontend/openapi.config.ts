const { generateService } = require("@umijs/openapi");

generateService({
  requestLibPath: "import request from '@/request'",
  schemaPath: "http://114.132.222.21:8101/api/v2/api-docs",
  serversPath: "./src",
});
