import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";
import { grpc } from "@improbable-eng/grpc-web";
import Metadata = grpc.Metadata;

const debug = false;

// 'myTransport' is configured to send Browser cookies along with cross-origin requests.
const withCredsTransport = grpc.CrossBrowserHttpTransport({
  withCredentials: true,
});

const shopNode1 = (address: string, metadata?: Metadata) =>
  new CatalogClientImpl(
    new GrpcWebImpl(address, {
      transport: NodeHttpTransport(),
      debug,
      metadata,
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

function getNodeClient(nextToken?: string) {
  if (typeof window === "undefined") {
    const m = new Metadata();
    m.set("authorization", nextToken);
    const m2 = nextToken ? m : undefined;
    return shopNode1(process.env.NEXT_PUBLIC_API_HOST, m2);
  }
  throw new Error("Can't create web client in web environment");
}

export default function getClient(nextToken?: string) {
  if (typeof window === "undefined") {
    return getNodeClient(nextToken);
  }
  return getWebClient();
}
