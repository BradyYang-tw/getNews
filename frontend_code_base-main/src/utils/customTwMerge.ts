import classNames, { ArgumentArray } from 'classnames';
import { extendTailwindMerge } from 'tailwind-merge';

/**
 * https://github.com/dcastil/tailwind-merge/issues/97
 */
const extendedTwMerge = extendTailwindMerge({
  classGroups: {},
});

export const customTwMerge = (...args: ArgumentArray): string =>
  extendedTwMerge(classNames(...args));
