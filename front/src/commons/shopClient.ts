import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";
import { grpc } from "@improbable-eng/grpc-web";

const debug = false;

// 'myTransport' is configured to send Browser cookies along with cross-origin requests.
const withCredsTransport = grpc.CrossBrowserHttpTransport({
  withCredentials: true,
});

const shopNode1 = (address: string) =>
  new CatalogClientImpl(
    new GrpcWebImpl(address, {
      transport: NodeHttpTransport(),
      debug,
    }),
  );

const shopWeb1 = (address) =>
  new CatalogClientImpl(
    new GrpcWebImpl(address, { transport: withCredsTransport, debug }),
  );

function getWebClient() {
  if (typeof window === "undefined") {
    throw new Error("Can't create web client in non-web environment");
  }
  return shopWeb1(process.env.NEXT_PUBLIC_API_HOST);
}

function getNodeClient() {
  if (typeof window === "undefined") {
    return shopNode1(process.env.NEXT_PUBLIC_API_HOST);
  }
  throw new Error("Can't create web client in web environment");
}

export default function getClient() {
  if (typeof window === "undefined") {
    return getNodeClient();
  }
  return getWebClient();
}
