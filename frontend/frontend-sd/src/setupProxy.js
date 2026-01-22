import { createProxyMiddleware } from "http-proxy-middleware";

export default function (app) {
  app.use(
    "/people",
    createProxyMiddleware({
      target: "http://localhost", 
      changeOrigin: true,
    })
  );

  app.use(
    "/devices",
    createProxyMiddleware({
      target: "http://localhost", 
      changeOrigin: true,
    })
  );

  app.use(
    "/auth",
    createProxyMiddleware({
      target: "http://localhost", 
      changeOrigin: true,
    })
  );

  app.use(
      "/monitoring",
      createProxyMiddleware({
          target: "http://localhost", 
          changeOrigin: true,
      })
  );
};
