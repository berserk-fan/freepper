import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";

const address = "http://localhost:8079/proxy";
const debug = false;

const shopNode1 = new CatalogClientImpl(
  new GrpcWebImpl(address, {
    transport: NodeHttpTransport(),
    debug,
  }),
);

const shopWeb1 = new CatalogClientImpl(new GrpcWebImpl(address, { debug }));

export default function getClient() {
  if (typeof window === "undefined") {
    return shopNode1;
  }
  return shopWeb1;
}
