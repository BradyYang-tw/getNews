declare module 'ov_viewer/app' {
  export function mount(container: HTMLElement | null, props: any): void
  export function unmount(): void
}

declare module '*.module.css' {
  const classes: { [key: string]: string }
  export default classes
}

declare module '*.css' {
  const classes: { [key: string]: string }
  export default classes
}

declare module '*.png' {
  const value: string
  export default value
}
