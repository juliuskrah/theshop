export class Product {
    id?: string;
    name: string | undefined;
    description: string | undefined
    imageSrc: URL | undefined;
    price: number = 0.0;
}