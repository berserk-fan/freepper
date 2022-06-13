import { CatalogClientImpl, GrpcWebImpl } from "apis/catalog.pb";
import { NodeHttpTransport } from "@improbable-eng/grpc-web-node-http-transport";

const debug = false;

const shopNode1 = (address: string) =>
  new CatalogClientImpl(
    new GrpcWebImpl(address, {
      transport: NodeHttpTransport(),
      debug,
    }),
  );
/*
 arg0 = CreateProduct(939c8757-8b45-4826-b00a-409e8013e38a,89dd0f98-a9b4-4a60-96c8-e70f009cdc8e,0.30600877127985093,Some(0.48890238876486025),List())
 */
const shopWeb1 = (address) =>
  new CatalogClientImpl(new GrpcWebImpl(address, { debug }));

export default function getClient() {
  if (typeof window === "undefined") {
    return shopNode1(process.env.NEXT_PUBLIC_API_HOST);
  }
  return shopWeb1(process.env.NEXT_PUBLIC_API_HOST);
}
