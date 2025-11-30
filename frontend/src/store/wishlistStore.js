import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { wishlistAPI } from '../services/api';
import toast from 'react-hot-toast';

export const useWishlistStore = create(
  persist(
    (set, get) => ({
      items: [],
      isLoading: false,

      fetchWishlist: async () => {
        set({ isLoading: true });
        try {
          const response = await wishlistAPI.get();
          // Handle both direct array and nested response structure
          const items = Array.isArray(response.data) 
            ? response.data 
            : (response.data?.data || []);
          set({ items, isLoading: false });
        } catch (error) {
          console.error('Failed to fetch wishlist:', error);
          set({ isLoading: false });
        }
      },

      addToWishlist: async (product) => {
        const { items } = get();
        const productId = typeof product === 'string' ? product : product.id;
        const exists = items.some(item => item.id === productId);
        
        if (exists) {
          toast.error('Already in wishlist');
          return;
        }

        // Create product object if only ID was passed
        const productToAdd = typeof product === 'string' ? { id: product } : product;
        
        // Optimistic update
        set({ items: [...items, productToAdd] });
        
        try {
          await wishlistAPI.add(productId);
          toast.success('Added to wishlist!', { icon: '❤️' });
        } catch (error) {
          // Revert on error
          set({ items: items.filter(item => item.id !== productId) });
          toast.error('Failed to add to wishlist');
          console.error('Failed to sync wishlist:', error);
        }
      },

      removeFromWishlist: async (productId) => {
        const { items } = get();
        
        // Optimistic update
        set({ items: items.filter(item => item.id !== productId) });
        
        try {
          await wishlistAPI.remove(productId);
          toast.success('Removed from wishlist');
        } catch (error) {
          console.error('Failed to sync wishlist:', error);
        }
      },

      clearWishlist: async () => {
        set({ items: [] });
        try {
          await wishlistAPI.clear();
        } catch (error) {
          console.error('Failed to clear wishlist:', error);
        }
      },

      isInWishlist: (productId) => {
        return get().items.some(item => item.id === productId);
      },

      getItemCount: () => {
        return get().items.length;
      },
    }),
    {
      name: 'wishlist-storage',
      partialize: (state) => ({ items: state.items }),
    }
  )
);
