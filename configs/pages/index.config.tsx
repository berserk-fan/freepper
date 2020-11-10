import { ru } from "../Translations";
import { CardData } from "../../src/pages";

export const carouselImages = [
  "https://picsum.photos/2000/900?random=1",
  "https://picsum.photos/2000/900?random=2",
  "https://picsum.photos/2000/900?random=3",
];

export const cardsData: CardData[] = [
  {
    id: "home-card-1",
    src: "https://picsum.photos/800/400?random=1",
    title: ru.home_cards_card1_title,
    text: ru.home_cards_card1_text,
    alt: ru.home_cards_card1_alt,
  },
  {
    id: "home-card-2",
    src: "https://picsum.photos/800/400?random=2",
    title: ru.home_cards_card2_title,
    text: ru.home_cards_card2_text,
    alt: ru.home_cards_card2_alt,
  },
  {
    id: "home-card-3",
    src: "https://picsum.photos/800/400?random=3",
    title: ru.home_cards_card3_title,
    text: ru.home_cards_card3_text,
    alt: ru.home_cards_card3_alt,
  },
  {
    id: "home-card-4",
    src: "https://picsum.photos/800/400?random=4",
    title: ru.home_cards_card4_title,
    text: ru.home_cards_card4_text,
    alt: ru.home_cards_card4_alt,
  },
];
