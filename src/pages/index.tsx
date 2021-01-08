import LayoutWithHeader from "../components/Layout/LayoutWithHeader";
import React from "react";

export type CardData = {
  id: string;
  src: string;
  title: string;
  text: string;
  alt: string;
};

export default function Home() {
  return <LayoutWithHeader value>Hello</LayoutWithHeader>;
}
