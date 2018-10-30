pragma solidity ^0.4.4;
import "./RoleController.sol";

/**
 * @title CommitteeMemberData
 * CommitteeMember data contract.
 */

contract CommitteeMemberData {
    // Array used to index and record the address of committee member.
    address[] private committeeMemberArray;

    RoleController private roleController;

    function CommitteeMemberData(address addr) public {
        roleController = RoleController(addr);
    }

    function isCommitteeMember(address addr) public constant returns (bool) {
        // Use LOCAL ARRAY INDEX here, not the RoleController data.
        // The latter one might lose track in the fresh-deploy or upgrade case.
        for (uint index = 0; index < committeeMemberArray.length; index++) {
            if (committeeMemberArray[index] == addr) {
                return true;
            }
        }
        return false;
    }

    function addCommitteeMemberFromAddress(address addr) public {
        if (isCommitteeMember(addr)) {
            return;
        }
        if (!roleController.checkPermission(tx.origin, roleController.MODIFY_COMMITTEE())) {
            return;
        }
        roleController.addRole(addr, roleController.ROLE_COMMITTEE());
        committeeMemberArray.push(addr);
    }

    function deleteCommitteeMemberFromAddress(address addr) public {
        if (!isCommitteeMember(addr)) {
            return;
        }
        if (!roleController.checkPermission(tx.origin, roleController.MODIFY_COMMITTEE())) {
            return;
        }
        roleController.removeRole(addr, roleController.ROLE_COMMITTEE());
        uint datasetLength = committeeMemberArray.length;
        for (uint index = 0; index < datasetLength; index++) {
            if (committeeMemberArray[index] == addr) { break; }
        }
        if (index != datasetLength-1) {
            committeeMemberArray[index] = committeeMemberArray[datasetLength-1];
        }
        delete committeeMemberArray[datasetLength-1];
        committeeMemberArray.length--;
    }

    function getDatasetLength() public constant returns (uint) {
        return committeeMemberArray.length;
    }

    function getCommitteeMemberAddressFromIndex(uint index) public constant returns (address) {
        return committeeMemberArray[index];
    }
}