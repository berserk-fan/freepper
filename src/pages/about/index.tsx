import { Box, Container, Typography } from "@material-ui/core";
import LayoutWithHeaderAndFooter from "../../components/Layout/LayoutWithHeaderAndFooter";

export default function AboutUs() {
  return (
    <LayoutWithHeaderAndFooter>
      <Container maxWidth="md">
        <Typography variant="h1">О нас</Typography>
        <Box>
          <Typography variant="h2">Секция 1</Typography>
          <Typography variant="body1">Коротко о вопросе</Typography>
          <Typography variant="body2">
            Товарищи! начало повседневной работы по формированию позиции влечет
            за собой процесс внедрения и модернизации направлений прогрессивного
            развития. Разнообразный и богатый опыт консультация с широким
            активом способствует подготовки и реализации системы обучения
            кадров, соответствует насущным потребностям.
          </Typography>
        </Box>
        <Box>
          <Typography variant="h2">Секция 2</Typography>
          <Typography variant="body1">Коротко о вопросе</Typography>
          <Typography variant="body2">
            Товарищи! начало повседневной работы по формированию позиции влечет
            за собой процесс внедрения и модернизации направлений прогрессивного
            развития. Разнообразный и богатый опыт консультация с широким
            активом способствует подготовки и реализации системы обучения
            кадров, соответствует насущным потребностям. Идейные соображения
            высшего порядка, а также реализация намеченных плановых заданий в
            значительной степени обуславливает создание существенных финансовых
            и административных условий. Идейные соображения высшего порядка.
          </Typography>
        </Box>
        <Typography variant="h2">Секция 3</Typography>
        <Typography variant="body1">
          С другой стороны реализация намеченных плановых заданий влечет за
          собой процесс внедрения и модернизации существенных финансовых и
          административных условий.
        </Typography>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}
