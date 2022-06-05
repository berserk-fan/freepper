import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";

const shopNode1 = new CatalogClientImpl(
  new GrpcWebImpl("https://api.pomo.in.ua", {
    transport: NodeHttpTransport(),
  }),
);

export default shopNode1;
