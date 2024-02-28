// Define the Cache interface
export interface Cache {
    contains(key: string): boolean;
    get(key: string): any | undefined;
    set(key: string, data: any): void;
    delete(key: string): void;
}

// Implement the Cache class
export class CacheImpl implements Cache {
    private cache: { [key: string]: any };

    constructor() {
        this.cache = {};
    }

    // Method to check if cache contains a key
    contains(key: string): boolean {
        return this.cache.hasOwnProperty(key);
    }

    // Method to get data from cache by key
    get(key: string): any | undefined {
        if (this.contains(key)) {
            return this.cache[key];
        } else {
            return undefined;
        }
    }

    // Method to set data in cache with a given key
    set(key: string, data: any): void {
        this.cache[key] = data;
    }

    // Method to delete data from cache by key
    delete(key: string): void {
        if (this.contains(key)) {
            delete this.cache[key];
        }
    }
}
