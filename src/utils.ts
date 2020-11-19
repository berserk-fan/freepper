export const pipe = <T extends any[], R>(
  fn1: (...args: T) => R,
  ...fns: Array<(a: R) => R>
) => {
  const piped = fns.reduce(
    (prevFn, nextFn) => (value: R) => nextFn(prevFn(value)),
    (value) => value
  );
  return (...args: T) => piped(fn1(...args));
};

export function pathName1<T, K extends keyof T>(t: T, key1: K) {
  return `${key1}`;
}

export function pathName<T, K extends keyof T, U extends keyof T[K]>(
  t: T,
  key1: K,
  key2: U
) {
  return `${key1}.${key2}`;
}
