/**
 * API Cache
 */
const CacheFacade = Java.type("org.eclipse.dirigible.components.api.cache.CacheFacade");
const cache = new CacheFacade();

export class Cache {

    public static contains(key: string): boolean {
        return (cache.contains(key));
    }

    public static get(key: any): any | undefined {
        return cache.get(key);
    }

    public static set(key: string, data: any): void {
        cache.set(key, data);
    }

    public static delete(key: string): void {
        cache.delete(key);
    }

    public static clear(): void {
        cache.clear();
    }
}
