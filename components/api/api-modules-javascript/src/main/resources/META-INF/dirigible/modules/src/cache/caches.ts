/**
 * API Cache
 */
const Cache = Java.type("org.eclipse.dirigible.components.api.cache.CacheFacade");

export class Caches {

    public static contains(key: string): boolean {
        return (Cache.contains(key));
    }

    public static get(key: string): string | undefined {
        return Cache.get(key);
    }

    public static set(key: string, data: any): void {
        Cache.set(key, data);
    }

    public static delete(key: string): void {
        Cache.delete(key);
    }

    public static clear(): void {
        Cache.clear();
    }
}
