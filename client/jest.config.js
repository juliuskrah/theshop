module.exports = {
  collectCoverage: true,
  collectCoverageFrom: ["src/**/*.{js}", "!**/node_modules/**"],
  coverageReporters: ["html", "text-summary"],
  setupFilesAfterEnv: []
};
