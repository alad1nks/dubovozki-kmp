const e2eWebPort = process.env.DUBOVOZKI_E2E_WEB_PORT;

if (e2eWebPort && config.devServer) {
    config.devServer.port = Number(e2eWebPort);
}
