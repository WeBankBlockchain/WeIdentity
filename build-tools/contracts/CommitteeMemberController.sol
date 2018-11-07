pragma solidity ^0.4.4;
import "./CommitteeMemberData.sol";
import "./RoleController.sol";

/**
 * @title CommitteeMemberController
 * Issuer contract manages authority issuer info.
 */

contract CommitteeMemberController {

    CommitteeMemberData private committeeMemberData;
    RoleController private roleController;

    // Event structure to store tx records
    uint constant private OPERATION_ADD = 0;
    uint constant private OPERATION_REMOVE = 1;
    uint constant private RETURN_CODE_SUCCESS = 0;
    uint constant private RETURN_CODE_FAILURE_ALREADY_EXISTS = 500251;
    uint constant private RETURN_CODE_FAILURE_NOT_EXIST = 500252;
    uint constant private RETURN_CODE_FAILURE_NO_PERMISSION = 500253;
    event CommitteeRetLog(uint operation, uint retCode, address addr);

    // Constructor.
    function CommitteeMemberController(
        address committeeMemberDataAddress,
        address roleControllerAddress
    )
        public 
    {
        committeeMemberData = CommitteeMemberData(committeeMemberDataAddress);
        roleController = RoleController(roleControllerAddress);
    }
    
    function addCommitteeMember(address addr) public {
        if (committeeMemberData.isCommitteeMember(addr)) {
            CommitteeRetLog(OPERATION_ADD, RETURN_CODE_FAILURE_ALREADY_EXISTS, addr);
            return;
        } else if (!roleController.checkPermission(tx.origin, roleController.MODIFY_COMMITTEE())) {
            CommitteeRetLog(OPERATION_ADD, RETURN_CODE_FAILURE_NO_PERMISSION, addr);
            return;
        } else {
            committeeMemberData.addCommitteeMemberFromAddress(addr);
            CommitteeRetLog(OPERATION_ADD, RETURN_CODE_SUCCESS, addr);
        }
    }
    
    function removeCommitteeMember(address addr) public {
        if (!committeeMemberData.isCommitteeMember(addr)) {
            CommitteeRetLog(OPERATION_REMOVE, RETURN_CODE_FAILURE_NOT_EXIST, addr);
            return;
        } else if (!roleController.checkPermission(tx.origin, roleController.MODIFY_COMMITTEE())) {
            CommitteeRetLog(OPERATION_REMOVE, RETURN_CODE_FAILURE_NO_PERMISSION, addr);
            return;
        } else {
            committeeMemberData.deleteCommitteeMemberFromAddress(addr);
            CommitteeRetLog(OPERATION_REMOVE, RETURN_CODE_SUCCESS, addr);
        }
    }

    function getAllCommitteeMemberAddress() public constant returns (address[]) {
        // Per-index access
        uint datasetLength = committeeMemberData.getDatasetLength();
        address[] memory memberArray = new address[](datasetLength);
        for (uint index = 0; index < datasetLength; index++) {
            memberArray[index] = committeeMemberData.getCommitteeMemberAddressFromIndex(index);
        }
        return memberArray;
    }

    function isCommitteeMember(address addr) public constant returns (bool) {
        return committeeMemberData.isCommitteeMember(addr);
    }
}