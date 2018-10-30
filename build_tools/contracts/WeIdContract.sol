pragma solidity ^0.4.4;

contract WeIdContract{

    mapping(address => uint) changed;

	modifier onlyOwner(address identity, address actor) {
      require (actor == identity);
      _;
    }
    
    event WeIdAttributeChanged(
      address indexed identity,
      bytes32 key,
      bytes value,
      uint previousBlock,
      int updated
    );

    function getLatestRelatedBlock(address identity) public constant returns (uint) {
        return changed[identity];
    }

    function setAttribute(address identity, bytes32 key,bytes value,int updated) public onlyOwner(identity, msg.sender){
    	WeIdAttributeChanged(identity, key, value, changed[identity],updated);
        changed[identity] = block.number;
    }
    
    function isIdentityExist(address identity) public constant returns (bool) {
    	if(0x0 != identity && 0 != changed[identity]){
    		return true;
    	}
    	return false;
    }
}