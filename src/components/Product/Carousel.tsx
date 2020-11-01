import React, {memo, useCallback, useState} from "react";
import styles from "./[productId].module.css";
import {useKeenSlider} from "keen-slider/react";
import {Flex} from "../styled";

export const Carousel = memo(({images}: { images: Array<string> }) => {
	const [details, setDetails] = useState(null);
	const [sliderRef, slider] = useKeenSlider({
		loop: true,
		slides: images.length,
		move(s) {
			setDetails(s.details());
		},
		initial: 2
	});

	const positionStyle = useCallback((index) => {
		if (!details) return {};
		const position = details.positions[index];
		const x = details.widthOrHeight * position.distance;
		const scale_size = 0.7;
		const scale = 1 - (scale_size - scale_size * position.portion);
		return {
			transform: `translate3d(${x}px, 0px, 0px) scale(${scale})`,
			WebkitTransform: `translate3d(${x}px, 0px, 0px) scale(${scale})`
		};
	}, [details])


	return (
		<Flex grow column>
			{/*@ts-ignore*/}
			<div ref={sliderRef}
				 className={`keen-slider ${styles.zoomOut}`}>
				{images.map((src, index) => (
					<div
						key={index}
						style={positionStyle(index)}
						className={`keen-slider__slide ${styles.zoomOut__slide}`}
					>
						<img src={src}/>
					</div>
				))}
			</div>
			{slider && (
				<div className={styles.dots}>
					{[...Array(slider.details().size).keys()].map(index => {
						return (
							<button
								key={index}
								onClick={() => {
									slider.moveToSlideRelative(index);
								}}
								className={`${styles.dot} ${index === details.relativeSlide && styles.dot_active}`}
							/>
						);
					})}
				</div>
			)}
		</Flex>
	)
})
