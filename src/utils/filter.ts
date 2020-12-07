type IntFilter = number | [number, number] //closed interval
type StringFilter = string | string[] //oneof
type ArrayFilter<T> = T[] //intersection is not empty
type DefaultFilter<T> = T //default

export type Filter<T> =
    T extends number ? IntFilter :
    T extends string ? StringFilter :
    T extends any[] ? ArrayFilter<T> :
    T extends object ? {[P in keyof T]?: Filter<T[P]>} : //recurse
    T extends Function ? never :
    DefaultFilter<T>

export function toPredicate<T>(filter: Filter<T>): (t:T) => boolean {
    return (t) => {
        switch (typeof t) {
            case "undefined":
                return true
            case "number": {
                if(Array.isArray(filter)) {
                    const range = (filter as [number,number])
                    return range[0] <= t && t <= range[1]
                } else {
                    return (filter as number) === t
                }
            }
            case "string":
                if(Array.isArray(filter)) {
                    return !!(filter as string[]).find(v => v === t)
                } else {
                    return (filter as string) === t
                }
            case "object": {
                if(Array.isArray(t)) {
                    return t.find(el => !!(filter as T[]).find(el2 => el == el2))
                } else {
                    const filter_ = (filter as {[P in keyof T]?: Filter<T[P]>})
                    return Object.entries(filter_).map(x => {
                        const key = x[0]
                        const predicate = toPredicate(x[1])
                        return predicate(t[key])
                    }).reduce((a,b) => a && b)
                }
            }
            default:
                return (filter as T) === t
        }
    }
}


