import styled from 'styled-components'

export type OffsetArgument = Array<number|string>|number|string|undefined

export interface IFlexProps {
	align?:string,
	justify?:string,
	grow?:boolean,
	basis?:string,
	rowReverse?:boolean,
	noWrap?:boolean,
	column?:boolean,
	height?:string,
	width?:string,
	background?:string,
	position?:string,
	p?:OffsetArgument,
	m?:OffsetArgument;
}

export const Flex = styled.div<IFlexProps>`
    display: flex;
    flex-direction: row;
    flex-wrap:wrap;
    ${ ( { align } ) => align && `align-items: ${ align };` }
    ${ ( { justify } ) => justify && `justify-content: ${ justify };` }
    ${ ( { grow } ) => grow && 'flex-grow:1;' }
    ${ ( { basis } ) => basis && `flex-basis:${ basis };` }
    ${ ( { rowReverse } ) => rowReverse && 'flex-direction: row-reverse;' }
    ${ ( { noWrap } ) => noWrap && 'flex-wrap: nowrap;' }
    ${ ( { column } ) => column && 'flex-direction: column;' }
    ${ ( { height } ) => height && `height: ${ height };` }
    ${ ( { width } ) => width && `width: ${ width };` }

    ${ ( { background } ) => background && `background:${ background };` }
    ${ ( { position } ) => position && `position:${ position };` }
    ${ ( { p } ) => p && (
	p instanceof Array ? `padding: ${ p.map( num => `${ num }px` ).join( ' ' ) };`
		: `padding: ${ p }px;` ) }

    ${ ( { m } ) => m && (
	m instanceof Array ? `margin: ${ m.map( num => `${ num }px` ).join( ' ' ) };`
		: `margin: ${ m }px;` ) }
`;
