pragma solidity ^0.4.4;

/**
 * @title RoleController
 *  This contract provides basic authentication control which defines who (address)
 *  belongs to what specific role and has what specific permission.
 */

contract RoleController {
    /**
     * Role related Constants.
     */
    uint constant public ROLE_AUTHORITY_ISSUER = 100;
    uint constant public ROLE_COMMITTEE = 101;
    uint constant public ROLE_ADMIN = 102;

    /**
     * Operation related Constants.
     */
    uint constant public MODIFY_AUTHORITY_ISSUER = 200;
    uint constant public MODIFY_COMMITTEE = 201;
    uint constant public MODIFY_ADMIN = 202;
    uint constant public MODIFY_KEY_CPT = 203;

    mapping (address => bool) private authorityIssuerRoleBearer;
    mapping (address => bool) private committeeMemberRoleBearer;
    mapping (address => bool) private adminRoleBearer;

    function RoleController() public {
        authorityIssuerRoleBearer[msg.sender] = true;
        adminRoleBearer[msg.sender] = true;
        committeeMemberRoleBearer[msg.sender] = true;
    }

    /**
     * Public common checkPermission logic.
     */
    function checkPermission(
        address addr,
        uint operation
    ) public constant returns (bool) 
    {
        if (operation == MODIFY_AUTHORITY_ISSUER) {
            if (adminRoleBearer[addr] || committeeMemberRoleBearer[addr]) {
                return true;
            }
        }
        if (operation == MODIFY_COMMITTEE) {
            if (adminRoleBearer[addr]) {
                return true;
            }
        }
        if (operation == MODIFY_ADMIN) {
            if (adminRoleBearer[addr]) {
                return true;
            }
        }
        if (operation == MODIFY_KEY_CPT) {
            if (authorityIssuerRoleBearer[addr]) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add Role.
     */
    function addRole(
        address addr,
        uint role
    ) public 
    {
        if (role == ROLE_AUTHORITY_ISSUER) {
            if (checkPermission(tx.origin, MODIFY_AUTHORITY_ISSUER)) {
                authorityIssuerRoleBearer[addr] = true;
            }
        }
        if (role == ROLE_COMMITTEE) {
            if (checkPermission(tx.origin, MODIFY_COMMITTEE)) {
                committeeMemberRoleBearer[addr] = true;
            }
        }
        if (role == ROLE_ADMIN) {
            if (checkPermission(tx.origin, MODIFY_ADMIN)) {
                adminRoleBearer[addr] = true;
            }
        }
    }

    /**
     * Remove Role.
     */
    function removeRole(
        address addr,
        uint role
    ) public 
    {
        if (role == ROLE_AUTHORITY_ISSUER) {
            if (checkPermission(tx.origin, MODIFY_AUTHORITY_ISSUER)) {
                authorityIssuerRoleBearer[addr] = false;
            }
        }
        if (role == ROLE_COMMITTEE) {
            if (checkPermission(tx.origin, MODIFY_COMMITTEE)) {
                committeeMemberRoleBearer[addr] = false;
            }
        }
        if (role == ROLE_ADMIN) {
            if (checkPermission(tx.origin, MODIFY_ADMIN)) {
                adminRoleBearer[addr] = false;
            }
        }
    }

    /**
     * Check Role.
     */
    function checkRole(
        address addr,
        uint role
    ) public constant returns (bool) 
    {
        if (role == ROLE_AUTHORITY_ISSUER) {
            return authorityIssuerRoleBearer[addr];
        }
        if (role == ROLE_COMMITTEE) {
            return committeeMemberRoleBearer[addr];
        }
        if (role == ROLE_ADMIN) {
            return adminRoleBearer[addr];
        }
    }
}
