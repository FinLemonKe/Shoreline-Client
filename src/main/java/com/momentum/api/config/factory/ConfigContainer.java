package com.momentum.api.config.factory;

import com.momentum.api.config.Config;
import com.momentum.api.registry.ILabeled;
import com.momentum.api.registry.IRegistry;
import com.momentum.api.registry.Registry;

import java.util.Collection;
import java.util.HashMap;
/**
 * Configuration container implementation for {@link Config}. Supports adding,
 * removing, and retrieving through the use of a {@link Registry} which is
 * backed by a {@link HashMap}.
 *
 * @author linus
 * @since 03/21/2023
 *
 * @see com.momentum.api.module.Module
 */
public class ConfigContainer extends Registry<Config<?>> {

    /**
     * Default constructor. Uses {@link ReflectionConfigFactory} to populate
     * the register through reflection
     */
    public ConfigContainer()
    {

        // populate
        ReflectionConfigFactory factory = new ReflectionConfigFactory();
        register = factory.build(getClass());
    }

    /**
     * Registers the given data to a register, which can later be retrieved
     * using {@link IRegistry#retrieve(String)}
     *
     * @param l The data label
     * @param data The data
     * @return The registered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    @Override
    public Config register(String l, Config data) {

        // null check
        if (data == null)
        {
            throw new NullPointerException(
                    "Null data not supported in registry");
        }

        // add to register
        data.setContainer(this);      // update data container
        return register.put(data.getLabel(), data);
    }

    /**
     * Registers the given data to a register, which can later be retrieved
     * using {@link IRegistry#retrieve(String)}
     *
     * @param data The data
     * @return The registered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    @Override
    public Config register(Config data) {

        // null check
        if (data == null)
        {
            throw new NullPointerException(
                    "Null data not supported in registry");
        }

        // add to register
        data.setContainer(this);      // update data container
        register.put(data.getLabel(), data);
        return data;
    }

    /**
     * Unregisters the given data to a register, which removes its mapping from
     * the register. Unregistering data also frees its {@link ILabeled}.
     *
     * @param data The data
     * @return The unregistered data
     * @throws NullPointerException if data is <tt>null</tt>
     */
    @Override
    public Config<?> unregister(Config data) {

        // null check
        if (data == null)
        {
            throw new NullPointerException(
                    "Null data not supported in registry");
        }

        // remove from register
        data.setContainer(null);      // update data container
        return register.remove(data.getLabel());
    }

    /**
     * Returns a {@link Collection} of {@link Config} in the registry. This
     * value is equal to {@link HashMap#values()}.
     *
     * @return A list of configs
     */
    public Collection<Config<?>> getConfigs() {

        // value list in map
        return register.values();
    }
}
