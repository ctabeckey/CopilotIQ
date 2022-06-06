package llc.nanocontext.copilotiq;

/**
 Define the Class and method(s) signature(s) for a utility
 that provides the following functionality:


 Given a set of 'pseudo-paths' such as


 `/opt/util/labs`,
 `/opt/util/labs/java`,
 `/home/users/bob`,
 `/home/users/joe`, etc.


 we want subsequently to be able to find out whether a given 'pseudo-path'
 would belong to the tree defined by the paths originally given (eg, /home/users would belong, /opt/util/labs/python and /root/foo would not).

 CTB? the meaning of "belong to" is not clearly defined. It has been taken to mean that
 a given path must be a parent, that is assuming that the paths define zero to many hierarchies.

 Provide only the signature(s), not the implementation.
 *
 */
public abstract class PseudoPath {

    /**
     * Construct an instance given zero to many path elements
     * @param paths an array of paths to match to
     */
    public PseudoPath(final String ... paths) {

    }

    /**
     * true if the given path is a parent of any of the
     * given paths.
     * @return
     */
    abstract boolean isParent(final String path);
}
