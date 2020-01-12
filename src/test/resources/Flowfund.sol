pragma solidity 0.5.8;


contract Flowfund {


    event CampaignStarted(
        address contractAddress,
        address projectStarter,
        string projectTitle,
        string projectDesc,
        uint256 deadline,
        uint256 successUntil,
        uint256 goalAmount
    );

    function startCampaign(
        string calldata title,
        string calldata description,
        uint stages,
        uint durationInDays,
        uint successInDays,
        uint amountToRaise
    ) external {


    }                                                                                                                                   


}

