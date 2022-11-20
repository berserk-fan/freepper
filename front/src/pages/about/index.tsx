import React from "react";
import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";
import Image from "next/image";
import Grid from "@mui/material/Grid";
import LayoutWithHeaderAndFooter from "../../components/Layout/LayoutWithHeaderAndFooter";

export default function AboutUs() {
  return (
    <LayoutWithHeaderAndFooter>
      <Container maxWidth="md">
        <Box marginBottom={10}>
          <Typography variant="h2" component="h1">
            О нас
          </Typography>
          <Grid container spacing={2} alignItems="flex-start">
            <Grid item xs={12} sm={6}>
              <Image src="/about-us-founder.jpg" width={500} height={500} />
            </Grid>
            <Grid item xs={12} sm={6}>
              <Box>
                <Typography variant="h4" component="h2">
                  Создатель
                </Typography>
                <Typography variant="body1">
                  Меня зовут Лика. Моя любовь к животным была настолько большой,
                  что после недолгих раздумий я решила связать с этим свою
                  жизнь. Сейчас `Погладить можно?` помогает клиентам и питомцам
                  создать дома уют и комфорт, вещи, такие для нас ценные.
                </Typography>
                <Typography variant="h4" component="h2">
                  О качестве
                </Typography>
                <Typography>
                  <b>
                    Мы сами делаем наши лежанки и контролируем качество каждого
                    товара.
                  </b>{" "}
                  Мы используем только качественные материалы. У нас крайне
                  низкий уровень возвратов, плохих отзывов и прочих неприяных
                  вещей, и мы следим за тем, чтобы так было и дальше.
                </Typography>
                <Typography variant="h4" component="h2">
                  В завершение
                </Typography>
                <Typography>
                  Спасибо, что интересуетесь нашей компанией. Мы надеемся, что
                  эта информация помогла вам узнать нас немножко лучше, и на то,
                  что мы сможем помочь вам в невсегда легком деле создания
                  комфорта для вашего питомца и уюта в вашем доме.
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Box>
      </Container>
    </LayoutWithHeaderAndFooter>
  );
}
